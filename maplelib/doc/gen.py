#!bin/bash
# -*- coding: utf-8 -*-

# def is_palindrome(n):
#     a=str(n)
#     le=len(a)
#     for i in range(le//2):
#         if a[i]!=a[le-1-i]:
#             return False
#     return True

# output = filter(is_palindrome, range(1, 1000))
# print(list(output))


import re
import io
import sys



fp = open('field.txt',encoding='utf-8')
str=fp.read();

pattern=re.compile(r'\s+(.+?)\s+(\d+?)\s+(yes|no)\s+(yes|no)\s+(.+?)\s*(.*?)\n')

iter = pattern.finditer(str)

for i in iter:
	g=i.group();
	print(g.strip(' \n'));


