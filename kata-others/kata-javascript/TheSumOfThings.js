
var readline = require('readline');
var rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
 });

rl.on('line', function(line){
    const str = line.split(' ')
    var result = 0
    for (const i of str) {
        if (i.length == 1 && isNaN(i)) result += i.charCodeAt(0) 
        else if (i.startsWith("0b")) result += parseInt(i.slice(2), 2) 
        else if (i.startsWith("0o")) result += parseInt(i.slice(2), 8) 
        else if (i.startsWith("0x")) result += parseInt(i.slice(2), 16) 
        else result += Number(i) 
    }
    console.log(result)
})
