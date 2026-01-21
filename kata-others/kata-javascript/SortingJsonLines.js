var readline = require('readline');
var rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
 });

 /*
{"Bentley.G":{"balance":2134,"account_no":233831255}}
{"Alton.K":{"balance":9315,"account_no":203123613,"extra":{"balance":131}}}
{"Bancroft.M":{"balance": 233,"account_no":287655771101},"extra":{"balance":98}}
*/
const list = []
rl.on('line', function(line){
    const jl = JSON.parse(line)
    const accountKey = Object.keys(jl).filter(k => k !== "extra")[0];
    const accountData = jl[accountKey];

    let balance;
    if (accountData && accountData.extra && accountData.extra.balance !== undefined) {
        balance = accountData.extra.balance; // inner extra
    } else if (jl.extra && jl.extra.balance !== undefined) {
        balance = jl.extra.balance; // outer extra
    } else if (accountData) {
        balance = accountData.balance; // normal balance
    }
    list.push({ name: accountKey, balance: balance })
});

rl.on('close', () => {
    list.sort((a, b) => a.balance - b.balance)
    for (const { name, balance } of list) {
        console.log(`${name}: ${balance.toLocaleString('en-US')}`)
    }
})