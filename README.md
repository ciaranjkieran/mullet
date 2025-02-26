# Mullet

Mullet is a productivity app designed to help users organise their tasks and activities through customisable modes. By allowing users to compartmentalise different aspects of their lives—work, personal projects, fitness, and more—Mullet enhances focus and efficiency.


## The Problem It Solves
Traditional to-do lists can become overwhelming, blending all tasks together. Mullet solves this by enabling users to switch between "modes," ensuring they stay focused on what matters in the moment. This structure supports work-life balance and productivity by reducing mental clutter.

## My Role
I am the sole developer of Mullet, responsible for:
- Designing and implementing the **full-stack architecture**.
- Building the **Django REST Framework backend**.
- Creating the **Android app in Kotlin** with modern development practices.
- Implementing data synchronisation, offline support, and background processing.

## Tech Stack
- **Backend:** Django REST Framework (DRF) with SQLite
- **Mobile App:** Android (Kotlin)
- **State Management:** Jetpack, ViewModel 
- **Dependency Injection:** Hilt
- **Background Processing:** WorkManager
- **Database:** Room (for local storage)

## Features
- **Modes & Tasks:** Organise tasks into different modes for better workflow management.
- **Task Synchronisation:** Keep tasks updated across devices with background sync.
- **Offline Support:** Manage tasks even without an internet connection.
- **Material 3 Design:** Uses modern UI principles for an intuitive user experience.

### Running the Backend
```sh
 cd backend
 python -m venv venv
 source venv/bin/activate  # On Windows, use `venv\Scripts\activate`
 pip install -r requirements.txt
 python manage.py runserver
```

### Running the Android App
1. Open Android Studio and load the `android` folder.
2. Sync Gradle and ensure all dependencies are installed.
3. Run the project on an emulator or connected device.

## Contributing
Contributions welcome! To contribute:
1. Fork the repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Make your changes and commit (`git commit -m "Add new feature"`).
4. Push to the branch (`git push origin feature-branch`).
5. Open a pull request.

## Future Developments

Planned features for upcoming versions of Mullet:

- **Projects & Goals** – Manage goals and projects by breaking them into tasks.
- **Timer Feature** – Built-in Pomodoro-style timer to help users stay focused while working.
- **Notes** – Quick note-taking to store ideas or reminders within each mode.
- **User Authentication** – Secure login system.
- **UI/UX Enhancements** – Improve the app’s design by refining colors, typography, spacing, and animations to create a more modern and user-friendly experience.

These features are in the pipeline and will be gradually introduced as development progresses.

## License
This project is shared publicly for portfolio purposes. Feel free to explore the code, but please do not use it for commercial purposes.

## Contact
For any questions or support, reach out at ciaranjohnkieran@gmail.com or open an issue on GitHub.

## Screenshots

<table width="100%" align="center">
  <tr>
    <td width="50%" align="center" style="padding:10px;">
      <img src="assets/screenshots/Work%20mode%20edit%20task.png" width="85%">
    </td>
    <td width="50%" align="center" style="padding:10px;">
      <img src="assets/screenshots/House%20task%20list.png" width="85%">
    </td>
  </tr>
    <tr>
    <td width="50%" align="center" style="padding:10px;">
      <img src="assets/screenshots/Fitness%20mode%20edit%20task.png" width="85%">
    </td>
    <td width="50%" align="center" style="padding:10px;">
      <img src="assets/screenshots/Guitar%20mode%20add%20mode.png" width="85%">
    </td>
  </tr>
  <tr>
    <td width="50%" align="center" style="padding:10px; vertical-align: top;">
      <img src="assets/screenshots/All%20mode.png" width="85%">
    </td>
    <td width="50%" align="center" style="padding:10px; vertical-align: top;">
      <img src="assets/screenshots/Mode%20selector.png" width="85%">
    </td>
  </tr>
</table>





