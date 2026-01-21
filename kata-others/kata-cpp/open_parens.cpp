#include <string>
#include <iostream>

int main()
{
    std::string input;
    while (std::getline(std::cin, input))
    {
        std::string stack = "";
        for (char &c : input)
        {
            if (c == '(')
            {
                stack.push_back(c);
            }
            else
            {
                if (stack.empty())
                {
                    stack.push_back(c);
                    break;
                }
                else
                {
                    stack.pop_back();
                }
            }
        }
        if (stack.empty())
        {
            std::cout << "yes" << std::endl;
        }
        else
        {
            std::cout << "no" << std::endl;
        }
    }
    return 0;
}