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
    // Create Label
    UILabel *myLabel = [[UILabel alloc]initWithFrame:CGRectMake(10, 50, 200, 40)];
    [myLabel setBackgroundColor:[UIColor clearColor]];
    [myLabel setText:@"Hi Label"];
    [[self view] addSubview:myLabel];
    
    // Create Text Field
    UITextField *myTextField = [[UITextField alloc] initWithFrame:CGRectMake(10, 100, 200, 40)];
    [myTextField setBackgroundColor:[UIColor clearColor]];
    [myTextField setText:@"Hi Text Field"];
    [[self view] addSubview:myTextField];
    
    self.view.backgroundColor = [UIColor blueColor];
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
