//
//  ViewController.h
//  SimplePluginUI
//
//  Created by Erik Balthazar on 22/07/15.
//  Copyright (c) 2015 Erik Balthazar. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ViewController : UIViewController
@property (strong, nonatomic) IBOutlet UITextField *txtText;
@property (strong, nonatomic) IBOutlet UILabel *lblLabel;
@property (strong, nonatomic) IBOutlet UIButton *btnChangeText;

- (void)open:(UIViewController *)viewController;
- (IBAction)onClick:(id)sender;

@end

