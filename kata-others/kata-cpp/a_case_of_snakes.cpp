
#include <iostream>
#include <string>
#include <regex>
#include <vector>

int main()
{
    std::string input;
    while (std::getline(std::cin, input))
    {
        std::regex r("(?=[A-Z])");

        std::sregex_token_iterator it(input.begin(), input.end(), r, -1);
        std::sregex_token_iterator end;
        std::vector<std::string> parts(it, end);

        std::string first = parts[0];
        if (first.length() <= 3 && parts.size() > 2)
        {
            parts[0] = "";
        }
        for (auto &s : parts)
        {
            if (s != "")
            {
                s[0] = std::tolower(static_cast<unsigned char>(s[0]));
            }
        }
        std::string result;
        for (const auto &s : parts)
        {
            if (s != "")
            {
                if (!result.empty())
                    result += "_";
                result += s;
            }
        }
        std::cout << result << "\n";
    }
    return 0;
}