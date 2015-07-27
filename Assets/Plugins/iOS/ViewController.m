//
//  ViewController.m
//  SimplePluginUI
//
//  Created by Erik Balthazar on 22/07/15.
//  Copyright (c) 2015 Erik Balthazar. All rights reserved.
//

#import "ViewController.h"

@interface ViewController () {
    UITableView *table;
}
@end

@implementation ViewController

-(void)open:(UIViewController *)viewController {
    // Create Label
    UILabel *myLabel = [[UILabel alloc]initWithFrame:CGRectMake(30, 50, 300, 60)];
    [myLabel setBackgroundColor:[UIColor clearColor]];
    [myLabel setText:@"Hi Label"];
    [[self view] addSubview:myLabel];
    
    // Create Text Field
    UITextField *myTextField = [[UITextField alloc] initWithFrame:CGRectMake(10, 100, 200, 40)];
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
    self.view.backgroundColor = [UIColor grayColor];
    [viewController presentModalViewController:self animated:YES];
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

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 1;
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
