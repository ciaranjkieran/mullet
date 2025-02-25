"""
API Views for managing Modes and Tasks.

ViewSets:
- ModeViewSet: Handles CRUD operations for Modes.
- TaskViewSet: Handles CRUD operations for Tasks.
"""

from rest_framework.viewsets import ModelViewSet
from .models import Mode, Task
from .serializers import ModeSerializer, TaskSerializer


class ModeViewSet(ModelViewSet):
    """API endpoint for managing Modes."""
    queryset = Mode.objects.all()
    serializer_class = ModeSerializer


class TaskViewSet(ModelViewSet):
    """API endpoint for managing Tasks."""
    queryset = Task.objects.all()
    serializer_class = TaskSerializer
