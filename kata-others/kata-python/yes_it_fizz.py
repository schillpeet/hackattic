import fileinput

def main():
    for line in fileinput.input():
        n, m = map(int, line.split())
        for i in range(n, m + 1):
            fizz = i % 3 == 0
            buzz = i % 5 == 0
            if fizz and buzz: print('FizzBuzz')
            elif fizz: print('Fizz')
            elif buzz: print('Buzz')
            else: print(i)

if __name__ == "__main__":
    main()
