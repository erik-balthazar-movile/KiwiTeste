//
//  ViewController.h
//  SimplePluginUI
//
//  Created by Erik Balthazar on 22/07/15.
//  Copyright (c) 2015 Erik Balthazar. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ViewController : UIViewController
@property (weak, nonatomic) IBOutlet UITextField *txtText;
@property (weak, nonatomic) IBOutlet UILabel *lblLabel;
@property (weak, nonatomic) IBOutlet UIButton *btnChangeText;
@property (weak, nonatomic) IBOutlet UIButton *btnChangeView;
- (void)open:(UIViewController *)viewController;
- (IBAction)onClick:(id)sender;

@end

