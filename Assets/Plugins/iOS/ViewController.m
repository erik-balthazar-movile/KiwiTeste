//
//  ViewController.m
//  SimplePluginUI
//
//  Created by Erik Balthazar on 22/07/15.
//  Copyright (c) 2015 Erik Balthazar. All rights reserved.
//

#import "ViewController.h"

@interface ViewController ()

@end

@implementation ViewController

-(void)open:(UIViewController *)viewController {
    UIButton* cBtn = [UIButton init];
    cBtn.frame = CGRectMake(0, 0, 100, 100);
    [cBtn setTitle:@"botao" forState:UIControlStateNormal];
    [self.view addSubview:cBtn];
    [viewController presentModalViewController:self animated:YES];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
- (IBAction)onClick:(id)sender {
    _lblLabel.text = _txtText.text;
}
@end
