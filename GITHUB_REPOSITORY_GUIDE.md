# GitHub Repository Setup Guide

This guide explains how to publish the GTrack project on GitHub using the folder structure in the assignment.

## 1. Prepare the project folder

Make sure your folder looks like this:

```text
GTrack/
├── src/
│   ├── GradeBook.java
│   ├── GradeComponent.java
│   ├── GradeTracker.java
│   ├── InputReader.java
│   ├── MultiScoreComponent.java
│   ├── SingleScoreComponent.java
│   └── TransmutationTable.java
├── docs/
│   └── README.md
├── screenshots/
│   └── README.md
├── .gitignore
├── GITHUB_REPOSITORY_GUIDE.md
└── README.md
```

Add your actual project proposal PDF and UML diagram image to `docs/`. Add screenshots of the running application to `screenshots/`. Replace the team-member and contributor placeholders in `README.md` with your group's actual information.

## 2. Create a repository on GitHub

1. Sign in at [GitHub](https://github.com/).
2. Click the **+** button in the upper-right corner, then select **New repository**.
3. Enter a repository name, for example `GTrack-Semester-Grade-Tracker`.
4. Add a short description, such as `A Java console application for tracking semester grades.`
5. Choose **Public** or **Private**, depending on your instructor's requirements.
6. Because you already have a `README.md` in your project folder, leave **Add a README file** unchecked. You can also leave the license and `.gitignore` templates unchecked because the local project already includes a `.gitignore` file.
7. Click **Create repository**.

## 3. Upload your project using the GitHub website

This is the simplest method if you are new to Git:

1. Open the new, empty repository on GitHub.
2. Choose **Add file** → **Upload files**.
3. Open your project folder on your computer and select the project files and folders (`src`, `docs`, `screenshots`, `.gitignore`, `README.md`, and `GITHUB_REPOSITORY_GUIDE.md`). Drag them into the upload area. Upload the contents of the project folder, not a ZIP file containing the whole folder.
4. Wait for the files to finish uploading.
5. In the **Commit changes** area, enter a message such as `Add initial GTrack project files`.
6. Click **Commit changes**.

Check the repository home page. You should see the `src`, `docs`, and `screenshots` folders and the `README.md` displayed on the page.

## 4. Alternative: upload with Git commands

Use this method if Git is installed on your computer.

1. Open a terminal in the project folder.
2. Initialize Git and create the first commit:

   ```bash
   git init
   git add .
   git commit -m "Add initial GTrack project files"
   ```

3. On the GitHub repository page, copy the repository's HTTPS URL. It will look similar to `https://github.com/YOUR-USERNAME/GTrack-Semester-Grade-Tracker.git` (use your actual URL, not this example).
4. Connect your local folder to GitHub and push:

   ```bash
   git branch -M main
   git remote add origin https://github.com/YOUR-USERNAME/GTrack-Semester-Grade-Tracker.git
   git push -u origin main
   ```

   Replace the example URL with the HTTPS URL shown on your own GitHub repository page. GitHub may ask you to authenticate.

## 5. Updating the repository later

After changing files, use these commands from the project folder:

```bash
git add .
git commit -m "Describe your changes"
git push
```

Use a commit message that briefly explains what changed, such as `Update grade report` or `Add UML diagram`.

## 6. Final submission checklist

- [ ] Repository has the required name and the correct visibility.
- [ ] `README.md` is visible on the repository home page.
- [ ] All seven `.java` source files are inside `src/`.
- [ ] Project proposal PDF is inside `docs/`.
- [ ] UML diagram image is inside `docs/`.
- [ ] Screenshots of the running application are inside `screenshots/`.
- [ ] Team members, roles, and contributors in `README.md` are accurate.
- [ ] The Java program compiles and runs from the instructions in `README.md`.
