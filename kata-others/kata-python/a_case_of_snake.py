import fileinput
import re

def main():
    for hWord in fileinput.input():
        parts = re.split(r'(?=[A-Z])', hWord)
        prefix = parts[0] if len(parts[0]) >= 4 else ""
        words = [word.lower() for word in parts[1:]]
        result = [prefix] + words if prefix else words
        print('_'.join(result))

if __name__ == '__main__':
    main()