#!bin/bash
# -*- coding: utf-8 -*-

def is_palindrome(n):
    a=str(n)
    le=len(a)
    for i in range(le//2):
        if a[i]!=a[le-1-i]:
            return False
    return True

output = filter(is_palindrome, range(1, 1000))
print(list(output))