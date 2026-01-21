#include <iostream>
#include <string>
#include <sstream>

int get_num_of_things(std::string sub_string)
{
    std::stringstream ss;
    if (sub_string.length() > 2 && sub_string[0] == '0')
    {
        if (sub_string[1] == 'x')
        {
            return std::stoi(sub_string.substr(2), nullptr, 16);
        }
        else if (sub_string[1] == 'b')
        {
            return std::stoi(sub_string.substr(2), nullptr, 2);
        }
        else if (sub_string[1] == 'o')
        {
            return std::stoi(sub_string.substr(2), nullptr, 8);
        }
    }
    else if (sub_string.length() == 1)
    {
        if (std::isdigit(sub_string[0]))
        {
            return std::stoi(sub_string);
        }
        else
        {
            return int(sub_string[0]);
        }
    }
    return std::stoi(sub_string);
}

int main()
{
    std::string input;
    while (std::getline(std::cin, input))
    {
        std::istringstream iss(input);
        std::string sub_string;
        int result = 0;
        while (iss >> sub_string)
        {
            result += get_num_of_things(sub_string);
        }
        std::cout << result << std::endl;
    }
    return 0;
}
