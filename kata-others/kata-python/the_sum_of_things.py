import fileinput


def main():
    for line in fileinput.input():
        parts = line.split()
        result = 0
        for p in parts:
            try:
                result += int(p, 0)
            except:
                result += ord(p)
        print(result)

if __name__ == '__main__':
    main()