# InstaApp - Android Instagram Clone

A modern Android application built with Kotlin and Jetpack Compose that replicates core Instagram
functionality including posts, comments, likes, and user profiles.

## 🏗️ Architecture

This project follows **Clean Architecture** principles with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────────┐
│                Presentation Layer                     │
│  ┌─────────────────────────────────────────────┐     │
│  │           UI Components               │     │
│  │  • Screens (Feed, Profile, etc.)     │     │
│  │  • ViewModels                      │     │
│  │  • Navigation                      │     │
│  └─────────────────────────────────────────────┘     │
│                      ↓                          │
│                Domain Layer                       │
│  ┌─────────────────────────────────────────────┐     │
│  │        Use Cases                     │     │
│  │  • GetFeedPostsUseCase            │     │
│  │  • CreatePostUseCase               │     │
│  │  • LikePostUseCase                 │     │
│  │  • etc.                           │     │
│  │           ↓                         │     │
│  │     Domain Models                   │     │
│  │  • Post, Comment, User            │     │
│  │  • Repository Interfaces           │     │
│  └─────────────────────────────────────────────┘     │
│                      ↓                          │
│                 Data Layer                          │
│  ┌─────────────────────────────────────────────┐     │
│  │     Repositories (Implementations)  │     │
│  │  • PostRepositoryImpl             │     │
│  │  • CommentRepositoryImpl           │     │
│  │  • UserRepositoryImpl              │     │
│  │           ↓                         │     │
│  │   Data Sources                    │     │
│  │  • DummyDataSource               │     │
│  │  • AuthPreferencesManager          │     │
│  └─────────────────────────────────────────────┘     │
└─────────────────────────────────────────────────────────────┘
```

## 📱 Core Features

### 🔐 Authentication

- User registration and login
- Secure token-based authentication
- Persistent session management

### 📸 Post Management

- Create posts with images and captions
- View feed with posts from all users
- Like/unlike posts
- Delete own posts
- Real-time post updates

### 💬 Comment System

- Add comments to posts
- Reply to comments (nested threading)
- Delete own comments
- Edit own comments
- Real-time comment count synchronization

### 👤 User Profiles

- View user profiles
- Follow/unfollow users
- Display follower/following counts
- Profile customization

## 🛠️ Technology Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM + Clean Architecture
- **Navigation**: Compose Navigation
- **Async**: Coroutines + Flow
- **DI**: Koin
- **Data Storage**: SharedPreferences + In-memory dummy data

## 📁 Project Structure

```
app/src/main/java/com/adr/instaapp/
├── data/                    # Data Layer
│   ├── datasource/
│   │   └── DummyDataSource.kt      # Main data source
│   ├── local/
│   │   └── AuthPreferencesManager.kt  # Local storage
│   ├── model/
│   │   └── UserCredentials.kt        # Data models
│   └── repository/               # Repository implementations
│       ├── CommentRepositoryImpl.kt
│       ├── PostRepositoryImpl.kt
│       └── UserRepositoryImpl.kt
├── domain/                   # Domain Layer
│   ├── model/
│   │   ├── Comment.kt
│   │   ├── Post.kt
│   │   └── User.kt
│   ├── repository/            # Repository interfaces
│   │   ├── CommentRepository.kt
│   │   ├── PostRepository.kt
│   │   └── UserRepository.kt
│   └── usecase/              # Business logic
│       ├── CreateCommentUseCase.kt
│       ├── CreatePostUseCase.kt
│       ├── DeleteCommentUseCase.kt
│       ├── GetFeedPostsUseCase.kt
│       └── ... (other use cases)
├── presentation/             # Presentation Layer
│   ├── navigation/
│   │   └── InstaAppNavigation.kt
│   ├── screen/
│   │   ├── FeedScreen.kt
│   │   ├── ProfileScreen.kt
│   │   ├── PostDetailScreen.kt
│   │   ├── PostCreationScreen.kt
│   │   └── ...
│   ├── viewmodel/
│   │   ├── FeedViewModel.kt
│   │   ├── ProfileViewModel.kt
│   │   └── ...
│   └── theme/
│       ├── Theme.kt
│       └── Type.kt
└── di/                      # Dependency Injection
    ├── appModule.kt
    ├── dataModule.kt
    ├── domainModule.kt
    └── presentationModule.kt
```

### Running the App

1. Open Android Studio
2. Import the project
3. Sync Gradle
4. Run on emulator or physical device

## 📱 Key Screens

### 📋 Feed Screen

- Displays posts from all users including current user
- Real-time updates when new posts are created
- Like and comment interactions
- Pull-to-refresh functionality

### 👤 Profile Screen

- User's own posts grid
- Profile information display
- Follower/following counts

### 📸 Post Creation

- Image selection and upload
- Caption writing
- Post publishing with real-time feed updates

### 💬 Post Detail

- Full post view with image and caption
- Comments section with nested replies
- Like interactions

This project is for educational purposes to demonstrate Android development best practices and Clean
Architecture implementation.

---

