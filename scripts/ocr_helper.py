import json
import sys
import warnings

import numpy as np
from numpy import ones_like

warnings.filterwarnings('ignore')
import easyocr
import cv2


def easy_ocr(img_path):
    fin_result = []
    img = cv2.imread(img_path)

    reader = easyocr.Reader(['en'], gpu=False, verbose=False)

    results = reader.readtext(
        img,
        detail=1, # Set this to 0 for simple output
        allowlist='0123456789+-x÷:×*·'
    )

    lines = sorted(results, key=lambda x: x[0][0][1])

    # bbox=boundedBox [x,y], text=founded text, conf=confidence
    i=0
    for bbox, text, conf  in lines:
        numbers = text if text[0].isdigit() else text[1:]

        # 1. find only area of 'signs': Region Of Interest (roi)
        x_coords = [p[0] for p in bbox]
        y_coords = [p[1] for p in bbox]

        #x_min = min(x_coords)
        x_min = 0
        y_min = min(y_coords)
        y_max = max(y_coords)

        roi = img[y_min:y_max, x_min:x_min+31] # length of box=278, ~10%=sign=>28pixel per sign
        #cv2.imwrite(f"pictures/after_roi/sign_roi_{i}.png", roi)

        # 2. Clean up margins (numbers that look into our sign box): black mask
        hsv = cv2.cvtColor(roi, cv2.COLOR_BGR2HSV)
        h, s, v = cv2.split(hsv)
        black_mask = (v < 80) & (s < 60)
        clean = ones_like(roi) * 255 # completely white
        clean[black_mask] = roi[black_mask] # keep only black

        #cv2.imwrite(f"pictures/after_black_mask/sign_cm_{i}.png", clean)

        # 3. morphologic: Closing = Dilation -> Erosion
        # Closing := To fill small holes and gaps in objects while preserving their overall shape
        # Dilation: to join adjacent objectts, fill small holes, and enhance features
        # Erosion: To remove small noise, detach connected objects, and erode boundaries

        # preparation
        gray = cv2.cvtColor(clean, cv2.COLOR_BGR2GRAY)
        _, binary = cv2.threshold(gray, 200, 255, cv2.THRESH_BINARY_INV)

        kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (3, 3))
        closed = cv2.morphologyEx(binary, cv2.MORPH_CLOSE, kernel, iterations=1)

        #cv2.imwrite(f"pictures/after_closing/sign_closed_{i}.png", closed)

        # 4. find the operation: ÷
        # idea: it's the only sign that consists of 3 parts
        num_labels, labels, stats, _ = cv2.connectedComponentsWithStats(closed, connectivity=8)
        num_labels = num_labels - 1 # because label 0 = background
        sign = ""
        if num_labels == 3:
            #print(f"picture contains ÷ sign: {i}")
            #print(f"÷: {i}")
            sign = "÷"
        else:
            # inversion
            ocr_ready = cv2.bitwise_not(closed)

            # resize: from fx=4 to 6-8
            #ocr_input = cv2.resize(ocr_ready, None, fx=8, fy=8, interpolation=cv2.INTER_CUBIC)

            result = reader.readtext(ocr_ready, detail=1, allowlist='+-x')
            if result:
                bbox, sign, conf = result[0]
                #print(f"picture contains sign: {sign}")
                #cv2.imwrite(f"pictures/last_decision/sign_ls_{i}.png", ocr_ready)
            else:
                #expected = text
                #print(f"NO SIGN but expected: {expected[0]}")
                #cv2.imwrite(f"pictures/last_decision/sign_ls_failed_{i}.png", ocr_ready)

                found_minus = False
                found_plus = False

                # try to find minus
                h_proj = np.sum(binary > 0, axis=1)
                num_rows_with_pixels = np.sum(h_proj > 0)
                if num_rows_with_pixels <= 3:
                    found_minus = True
                    #print(f"-: {i}")
                    sign = "-"

                # try to find a plus
                v_proj = np.sum(binary > 0, axis=0)
                height, width = binary.shape
                threshold_h = int(0.2 * width)
                threshold_v = int(0.2 * height)

                if np.max(h_proj) > threshold_h and np.max(v_proj) > threshold_v:
                    found_plus = True
                    #print(f"+: {i}")
                    sign = "+"

                # rest is x
                if not found_minus and  not found_plus:
                    #print(f"x: {i}")
                    sign = "x"

        i += 1
        #fin_result = f"{sign}{numbers}"
        #print(fin_result)
        fin_result.append({
            "sign": sign,
            "numbers": numbers
        })
    return json.dumps(fin_result, ensure_ascii=False)

if __name__ == "__main__":
    res = easy_ocr(sys.argv[1])
    print(res)