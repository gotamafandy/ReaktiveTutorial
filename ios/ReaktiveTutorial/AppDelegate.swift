//
//  AppDelegate.swift
//  ReaktiveTutorial
//
//  Created by Fandy Gotama on 12/10/19.
//  Copyright © 2019 Adrena Teknologi Indonesia. All rights reserved.
//

import UIKit
import Core

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    var window: UIWindow?
    var dbHelper: DatabaseHelper!
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        try! setupDatabase()
        
        let controller = ViewController()
        
        controller.title = "Hello World"
        
        let navigationController = UINavigationController(rootViewController: controller)
        
        window = UIWindow(frame: UIScreen.main.bounds)
        window?.backgroundColor = .white
        window?.rootViewController = navigationController
        window?.makeKeyAndVisible()
        
        return true
    }

    func setupDatabase() throws {
        dbHelper = DatabaseHelper(dbName: "movies.db", sqlDriver: nil)
        
        #if DEBUG
        let databaseURL = try FileManager.default.url(for: .applicationSupportDirectory, in: .userDomainMask, appropriateFor: nil, create: true)
        
        print("DB PATH: \(databaseURL.path)")
        #endif
    }
}

