# json

This utility is a little like XPath to query strings in json.

Code JParse class has a parse() to visit json strings with a meta format of "{:,,,,[]", so meta words work like a path. In this case, you don't know the name of pairs.
For convenience, also there is a query() method to visit json nodes with names, like "abc:[2]:def". This could get "de" string from a json {abc:[1,2,{def:de}]}.

Welcome to use it and change it. :)

Refactoring code and change bugs about "\"". 
