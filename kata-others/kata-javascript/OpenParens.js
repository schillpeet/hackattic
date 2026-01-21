
var readline = require('readline');
var rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
 });

rl.on('line', function(line){
    const str = line.split('')
    const arr = []
    for (const i of str) {
        if (i == "(") {
            arr.push(i)
        } else {
            if (arr.length == 0) {
                arr.push(i)
                break
            } {
                arr.pop()
            }
        }
    }
    if (arr.length == 0) console.log("yes")
    else console.log("no")
})
