var readline = require('readline');
var rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
 });

rl.on('line', function(line){
    const result = line.match(/(.)\1*/g)
        .map(val => val.length <= 2 ? val : val.length + val[0])
        .join("")
    console.log(result)
});
