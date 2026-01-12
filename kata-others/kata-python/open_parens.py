import fileinput

def main():
    for line in fileinput.input():
        stack = []
        for i in list(line):
            if not stack or stack[-1] == i: stack.append(i)
            else: stack.pop()
        if stack: print('yes')
        else: print('no')

if __name__ == "__main__":
    main()
