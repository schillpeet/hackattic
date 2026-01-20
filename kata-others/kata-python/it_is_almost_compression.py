import fileinput
from itertools import groupby


def main():
    for line in fileinput.input():
        grouped_res = ["".join(group) for _, group in groupby(line.strip())]
        res = "".join([str(len(g)) + g[0] if len(g) > 2 else g for g in grouped_res])
        print(res)

if __name__ == '__main__':
    main()
