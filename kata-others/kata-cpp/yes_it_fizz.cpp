#include <iostream>
#include <string>
#include <sstream>

int main()
{
    std::string input;
    while (std::getline(std::cin, input))
    {
        // split string in start and end
        std::istringstream iss(input);
        std::string start, end;
        iss >> start >> end;

        int start_num = std::stoi(start);
        int end_num = std::stoi(end);
        for (int i = start_num; i <= end_num; ++i)
        {
            bool fizz = i % 3 == 0;
            bool buzz = i % 5 == 0;
            if (fizz && buzz)
                std::cout << "FizzBuzz\n";
            else if (fizz)
                std::cout << "Fizz\n";
            else if (buzz)
                std::cout << "Buzz\n";
            else
                std::cout << i << '\n';
        }
    }
    return 0;
}
