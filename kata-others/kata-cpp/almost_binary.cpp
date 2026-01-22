#include <string>
#include <iostream>
#include <algorithm>

int main()
{
    std::string input;
    while (std::getline(std::cin, input))
    {
        std::replace(input.begin(), input.end(), '#', '1');
        std::replace(input.begin(), input.end(), '.', '0');
        int result = std::stoi(input, nullptr, 2);
        std::cout << result << std::endl;
    }
    return 0;
}