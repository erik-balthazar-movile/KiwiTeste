//
//  ViewController.m
//  SimplePluginUI
//
//  Created by Erik Balthazar on 22/07/15.
//  Copyright (c) 2015 Erik Balthazar. All rights reserved.
//

#import "ViewController.h"

@interface ViewController () {
    UILabel* myLabel;
    UITextField *myTextField;
    UIButton* btnClose;
    UITableView* table;
}
@end

@implementation ViewController

-(void)open:(UIViewController *)viewController {
    // Create Label
    myLabel = [[UILabel alloc]initWithFrame:CGRectMake(30, 50, 300, 60)];
    [myLabel setBackgroundColor:[UIColor clearColor]];
    [myLabel setText:@"Hi Label"];
    [[self view] addSubview:myLabel];
    
    // Create Text Field
    myTextField = [[UITextField alloc] initWithFrame:CGRectMake(10, 100, 200, 40)];
    [myTextField setBackgroundColor:[UIColor whiteColor]];
    [myTextField setText:@"Hi Text Field"];
    [[self view] addSubview:myTextField];
    
    // Create Table View
    table = [[UITableView alloc] initWithFrame:CGRectMake(100, 20, 100, 200)];
    table.autoresizingMask = UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleWidth;
    table.delegate = self;
    table.dataSource = self;
    [table registerClass:[UITableViewCell class] forCellReuseIdentifier:@"Cell"];
    [table reloadData];
    [table setBackgroundColor:[UIColor whiteColor]];
    [[self view] addSubview:table];
    
    // Create close button
    btnClose = [[UIButton alloc] initWithFrame:CGRectMake(10, 130, 100, 100)];
    [btnClose setTitle:@"Close" forState:UIControlStateNormal];
    [btnClose addTarget:self action:@selector(closeButtonPressed) forControlEvents:UIControlEventTouchDown];
    [btnClose setBackgroundColor:[UIColor blackColor]];
    [[self view] addSubview:btnClose];
    
    self.view.backgroundColor = [UIColor grayColor];
    [viewController presentModalViewController:self animated:YES];
}

- (void)closeButtonPressed {
    
    //[self.view removeFromSuperview];
    if ([self respondsToSelector:@selector(presentingViewController)])
        [[self presentingViewController] dismissModalViewControllerAnimated:YES];
    else
        [[self parentViewController] dismissModalViewControllerAnimated:YES];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [table dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath:indexPath] ;
    
    if (cell == nil)
    {
        
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
    }
    cell.textLabel.text= @"Celula";
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 1;
}

- (void)willRotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration {
     if (UIInterfaceOrientationIsLandscape(toInterfaceOrientation)) {
         myLabel.frame = CGRectMake(30, 50, 300, 60);
         myTextField.frame = CGRectMake(10, 100, 200, 40);
         table.frame = CGRectMake(100, 20, 100, 200);
         btnClose.frame = CGRectMake(10, 130, 100, 100);
     } else {
         myLabel.frame = CGRectMake(30, 50, 300, 60);
         myTextField.frame = CGRectMake(20, 100, 200, 40);
         table.frame = CGRectMake(100, 60, 100, 200);
         btnClose.frame = CGRectMake(50, 50, 100, 100);
     }
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
