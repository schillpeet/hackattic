#include <string>
#include <iostream>

int main()
{
    std::string input;
    while (std::getline(std::cin, input))
    {
        std::string result = "";
        char prev = input[0];
        int count = 1;

        for (size_t i = 1; i <= input.length(); ++i)
        {
            if (i < input.length() && input[i] == prev)
            {
                count++;
            }
            else
            {
                if (count >= 3)
                {
                    result += std::to_string(count) + prev;
                }
                else
                {
                    for (int j = 0; j < count; j++)
                    {
                        result += prev;
                    }
                }

                if (i < input.length())
                {
                    prev = input[i];
                    count = 1;
                }
            }
        }
        std::cout << result << std::endl;
    }
    return 0;
}