import fileinput
import json
import re

'''
{"Bentley.G":{"balance":2134,"account_no":233831255}}
{"Alton.K":{"balance":9315,"account_no":203123613,"extra":{"balance":131}}}
{"Bancroft.M":{"balance": 233,"account_no":287655771101},"extra":{"balance":98}}
'''

def main():
    items = []
    for line in fileinput.input():
        jl = json.loads(line)
        
        name = next(iter(jl))
        data = jl[name]
        balance = (jl.get("extra") or data.get("extra", data)).get("balance")
        items.append((name, balance))

    for name, balance in sorted(items, key=lambda x: x[1]):
        print(f"{name}: {balance:,}")

if __name__ == '__main__':
    main()