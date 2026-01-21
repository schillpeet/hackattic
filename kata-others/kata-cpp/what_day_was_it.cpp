#include <iostream>
#include <string>

int main()
{
    std::string input;
    while (std::getline(std::cin, input))
    {
        long long time = std::stoi(input) * 86400; // seconds per day
        int day = (((time / 86400) + 4) % 7 + 7) % 7;
        const char *day_names[] = {
            "Sunday", "Monday", "Tuesday", "Wednesday",
            "Thursday", "Friday", "Saturday"};

        std::cout << day_names[day] << std::endl;
    }
    return 0;
}
