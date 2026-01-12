import fileinput

def main():
    #for line in fileinput.input():
    #    print(line)
    input_values = "8 16"
    start, end = map(int, input_values.split())
    for i in range(start, end+1):
        fizz = i % 3 == 0
        buzz = i % 5 == 0
        if fizz and buzz: print('fizz')
        elif fizz: print('fizz')
        elif buzz: print('buzz')
        else: print(i)



if __name__ == "__main__":
    main()
