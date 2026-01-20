import datetime
import fileinput

def main():
    for line in fileinput.input():
        print(datetime.datetime.fromtimestamp(int(line.strip()) * 86_400).strftime("%A"))

if __name__ == '__main__':
    main()