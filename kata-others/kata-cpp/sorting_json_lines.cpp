#include <iostream>
#include <string>
#include <algorithm>
#include <vector>
#include <locale>

/*
{"Bentley.G":{"balance":2134,"account_no":233831255}}
{"Alton.K":{"balance":9315,"account_no":203123613,"extra":{"balance":131}}}
{"Bancroft.M":{"balance": 233,"account_no":287655771101},"extra":{"balance":98}}
*/

struct Account
{
    std::string name;
    int balance;
};

struct comma_separator : std::numpunct<char>
{
    char do_thousands_sep() const override { return ','; }
    std::string do_grouping() const override { return "\3"; }
};

std::string get_name(std::string input)
{
    size_t start = 2;
    size_t end = input.find('"', start);
    std::string name = "";

    if (end != std::string::npos)
    {
        name = input.substr(start, end - start);
    }
    return name;
}

int get_balance(std::string input)
{
    std::string key_extra = "\"extra\":";
    size_t pos_extra = input.find(key_extra);

    // no extra
    if (pos_extra == std::string::npos)
    {
        std::string key = "\"balance\":";
        size_t pos = input.find(key);

        pos += key.length(); // jump to

        size_t start = input.find_first_of("-0123456789", pos);

        if (start != std::string::npos)
        {
            return std::stoi(input.substr(start));
        }
        return 0;
    }

    size_t search_start = pos_extra + key_extra.length();

    std::string key_balance = "\"balance\":";
    size_t pos_balance = input.find(key_balance, search_start);

    size_t value_start_pos = pos_balance + key_balance.length();
    size_t digit_pos = input.find_first_of("-0123456789", value_start_pos);

    if (digit_pos != std::string::npos)
    {
        return std::stoi(input.substr(digit_pos));
    }

    return 0;
}

int main()
{
    std::string input;
    std::vector<Account> list;
    while (std::getline(std::cin, input))
    {
        list.push_back({get_name(input), get_balance(input)});
    }
    std::sort(list.begin(), list.end(), [](const Account &a, const Account &b)
              { return a.balance < b.balance; });

    std::locale custom_locale(std::locale::classic(), new comma_separator);
    std::cout.imbue(custom_locale);
    for (const auto &acc : list)
    {
        std::cout << acc.name << ": " << acc.balance << std::endl;
    }
    return 0;
}
