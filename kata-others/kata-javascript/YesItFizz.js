
var readline = require('readline');
var rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
 });

rl.on('line', function(line){
    const nums = line.split(' ')
    for (let num = Number(nums[0]); num <= Number(nums[1]); num++) {
        if (num % 3 == 0 && num % 5 == 0) console.log("FizzBuzz")
        else if (num % 3 == 0) console.log("Fizz")
        else if (num % 5 == 0) console.log("Buzz")
        else console.log(num)
    }
});
