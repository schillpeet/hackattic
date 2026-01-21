#include <string>
#include <iostream>
#include <vector>

// no third party lib :-/
std::string base64_decode(const std::string &encode)
{
    const std::string base64_chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    std::string decoded;
    std::vector<int> T(256, -1);
    for (int i = 0; i < 64; i++)
        T[base64_chars[i]] = i;

    int val = 0, valb = -8;
    for (unsigned char c : encode)
    {
        if (T[c] == -1)
            break;
        val = (val << 6) + T[c];
        valb += 6;
        if (valb >= 0)
        {
            decoded.push_back(char((val >> valb) & 0xFF));
            valb -= 8;
        }
    }
    return decoded;
}

int main()
{
    std::string input;
    while (std::getline(std::cin, input))
    {
        std::string result = base64_decode(input);
        std::cout << result << "\n";
    }
    return 0;
}