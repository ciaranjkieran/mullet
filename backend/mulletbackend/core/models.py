"""
Defines database models for the Mullet app.

Models:
- Mode: Represents task categories (e.g., "All", "Work", "Play").
- Task: Represents user tasks assigned to a specific mode, including the default mode.
"""

from django.db import models


class Mode(models.Model):
    """
    Represents a category for tasks (e.g., "All", "Work", "Play").

    Attributes:
        title (str): The name of the mode.
        color (str): Hex code representing the mode's color.
        is_default (bool): Whether the mode is a system default (e.g., "All").
    """
    title = models.CharField(max_length=255)
    color = models.CharField(max_length=7, default="#FFFFFF")
    is_default = models.BooleanField(default=False)  # Indicates default mode (e.g., "All")

    def __str__(self):
        return self.title

    def delete(self, *args, **kwargs):
        """
        Prevents deletion of default modes (e.g., "All").
        Raises an exception if an attempt is made to delete a system mode.
        """
        if self.is_default:
            raise Exception("Default mode cannot be deleted.")
        super().delete(*args, **kwargs)


class Task(models.Model):
    """
    Represents a user task associated with a specific mode.

    Attributes:
        title (str): Task name.
        description (str, optional): Task details.
        mode (ForeignKey): The mode (category) the task belongs to, including the default mode.
        time_logged (int): Time spent on the task (in seconds).
        is_completed (bool): Whether the task has been marked as complete.
    """
    title = models.CharField(max_length=255)
    description = models.TextField(blank=True, null=True)
    mode = models.ForeignKey(Mode, on_delete=models.CASCADE, default=1)  
    time_logged = models.PositiveIntegerField(default=0)  # Stored in seconds.
    is_completed = models.BooleanField(default=False)

    def __str__(self):
        return self.title

