/**
 *  Algorithms and data structures (part 2). Home work 1.
 *  Problem 1: prefix function.
 *
 *  @author Aman Orazaev
 */

#include <string>
#include <iostream>
#include <vector>


template <typename T>
void PrintVector(T begin, T end) {
    while(begin != end) {
        std::cout << *begin++ << (begin + 1 == end ? "" : " ");
        
    }
    std::cout << "\n";
}



std::vector<size_t> GetPrefixFunction(const std::string& s) {
    std::vector<size_t> prefixFunction(s.size(), 0);

    for (size_t curent = 1; curent < s.size(); ++curent) {
        size_t j = prefixFunction[curent - 1];
        while (j != 0 && s[curent] != s[j]) {
            j = prefixFunction[j - 1];
        }

        if (s[curent] == s[j]) {
            prefixFunction[curent] = j + 1;
        }
    }
    
    return prefixFunction;
}



int main() {
    std::vector<size_t> pref = GetPrefixFunction("abacaba");
    PrintVector(pref.begin(), pref.end());

    pref = GetPrefixFunction(
            "abxzzabxhhhhabxzzabxk......................abxzzabxhhhhabxzzabxz");
    PrintVector(pref.begin(), pref.end());
    
    return 0;
}