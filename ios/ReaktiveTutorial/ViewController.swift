//
//  ViewController.swift
//  ReaktiveTutorial
//
//  Created by Fandy Gotama on 12/10/19.
//  Copyright © 2019 Adrena Teknologi Indonesia. All rights reserved.
//

import UIKit
import Core

class ViewController: UIViewController {
    private var _movies: [Movie]?
    private var _isRefreshing = false
    
    lazy var refreshControl: UIRefreshControl = {
        let v = UIRefreshControl()
        
        v.addTarget(self, action: #selector(refresh), for: .valueChanged)
        
        return v
    }()
    
    lazy var collectionView: UICollectionView = {
        let layout = UICollectionViewFlowLayout()
        
        layout.minimumLineSpacing = 20
        layout.scrollDirection = .vertical
        layout.sectionInset = UIEdgeInsets(top: 20, left: 10, bottom: 5, right: 10)
        
        let marginsAndInsets = layout.sectionInset.left + layout.sectionInset.right + layout.minimumInteritemSpacing * CGFloat(2 - 1)
        let itemWidth = ((UIScreen.main.bounds.size.width - marginsAndInsets) / CGFloat(2)).rounded(.down)
        
        let itemSize = CGSize(width: itemWidth, height: 250)
        
        layout.itemSize = itemSize
        
        let v = UICollectionView(frame: .zero, collectionViewLayout: layout)
        
        v.backgroundColor = UIColor(named: "ListBackground")
        v.delegate = self
        v.dataSource = self
        v.alwaysBounceVertical = true
        v.refreshControl = refreshControl
        v.translatesAutoresizingMaskIntoConstraints = false
        v.register(MovieCell.self, forCellWithReuseIdentifier: "MovieCell")
        
        return v
    }()
    
    private lazy var _viewModel: ListViewModelImpl<NSString, Movie> = {
        let delegate = UIApplication.shared.delegate as! AppDelegate
        
        let mapper = MoviesMapper()
        let service = MoviesCloudService(key: "b445ca0b", hostUrl: "https://www.omdbapi.com/", mapper: mapper)
        let cache = MovieSqlCache(db: delegate.dbHelper)
        let repository = MoviesRepositoryImpl<NSString>(service: service, cache: cache)
        let useCase = UseCaseImpl<NSString, NSArray>(repository: repository)
        
        let viewModel = ListViewModelImpl<NSString, Movie>(useCase: useCase, mapper: nil)
        
        return viewModel
    }()
    
    private lazy var _binding: ViewModelBinding = {
        return ViewModelBinding()
    }()
    
    deinit {
        _binding.dispose()
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        binding()
        
        view.addSubview(collectionView)
        
        NSLayoutConstraint.activate([
            collectionView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            collectionView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            collectionView.topAnchor.constraint(equalTo: view.topAnchor),
            collectionView.bottomAnchor.constraint(equalTo: view.bottomAnchor)
        ])
        
        _viewModel.inputs.get(request: "avenger")
    }
    
    // MARK - Selector
    @objc func refresh() {
        _viewModel.inputs.get(request: "avenger")
    }
    
    // MARK - Private
    private func binding() {
        
        _binding.subscribe(observable: _viewModel.outputs.loading) { [weak self] result in
            guard let strongSelf = self, let loading = result as? Bool else { return }
            
            strongSelf._isRefreshing = loading
            
            if loading {
                strongSelf.refreshControl.beginRefreshing()
            } else {
                strongSelf.refreshControl.endRefreshing()
            }
        }
        
        _binding.subscribe(observable: _viewModel.outputs.result) { [weak self] result in
            guard let strongSelf = self, let list = result as? [Movie] else { return }
            
            strongSelf._movies = list
            strongSelf.collectionView.reloadData()
        }
    }
}

extension ViewController: UICollectionViewDataSource, UICollectionViewDelegate {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return _movies?.count ?? 0
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "MovieCell", for: indexPath) as! MovieCell
        
        cell.movie = _movies?[indexPath.row]
        
        return cell
    }
    
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        let bottomEdge = scrollView.contentOffset.y + scrollView.frame.size.height;
        
        if (bottomEdge + 200 >= scrollView.contentSize.height && scrollView.contentOffset.y > 0 && !_isRefreshing) {
            _viewModel.inputs.loadMore(request: "avenger")
        }
    }
}
