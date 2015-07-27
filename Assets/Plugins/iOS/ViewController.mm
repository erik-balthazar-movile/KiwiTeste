//
//  ViewController.m
//  SimplePluginUI
//
//  Created by Erik Balthazar on 22/07/15.
//  Copyright (c) 2015 Erik Balthazar. All rights reserved.
//

#import "ViewController.h"

extern UIViewController* UnityGetGLViewController();

static ViewController* ourViewController;

extern "C" {
    
    void openOurViewController() {
        ourViewController = [[ViewController alloc] init];
        [ourViewController open:UnityGetGLViewController()];
    }
}