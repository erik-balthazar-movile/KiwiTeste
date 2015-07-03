//
//  PluginTest.m
//  PluginIOSTeste
//
//  Created by Erik Balthazar on 03/07/15.
//  Copyright (c) 2015 Erik Balthazar. All rights reserved.
//

#import <Foundation/Foundation.h>

char* cStringCopy(const char* string)
{
    if (string == NULL)
        return NULL;
    
    char* res = (char*)malloc(strlen(string) + 1);
    strcpy(res, string);
    
    return res;
}

extern "C"
{
    char* _helloWorld()
    {
        NSString* str = @"Hello World";
        return cStringCopy([str UTF8String]);
    }
}