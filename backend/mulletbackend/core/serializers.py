"""
Serializers for transforming database models into JSON representations.

Includes:
- ModeSerializer: Serializes Mode objects.
- TaskSerializer: Serializes Task objects with a related Mode.
"""

from rest_framework import serializers
from .models import Mode, Task

class ModeSerializer(serializers.ModelSerializer):
    """
    Serializer for the Mode model.
    
    Converts Mode instances to JSON and defines the fields that should be exposed via API.
    """
    class Meta:
        model = Mode
        fields = ['id', 'title', 'color']


class TaskSerializer(serializers.ModelSerializer):
    """
    Serializer for the Task model.
    
    - Uses `modeId` instead of `mode` for better frontend compatibility.
    - Ensures tasks reference a valid Mode instance.
    """
    modeId = serializers.PrimaryKeyRelatedField(source='mode', queryset=Mode.objects.all())
    
    class Meta:
        model = Task
        fields = ['id', 'title', 'description', 'modeId', 'is_completed']

    def create(self, validated_data):
        """Handles task creation logic."""
        return super().create(validated_data)
