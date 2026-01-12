import fileinput

def main():
    for line in fileinput.input():
        print(int(line.replace('#', '1').replace('.', '0'), 2))

if __name__ == "__main__":
    main()