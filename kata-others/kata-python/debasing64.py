import base64
import fileinput


def main():
    for line in fileinput.input():
        result = base64.b64decode(line).decode('utf-8')
        print(result)

if __name__ == '__main__':
    main()