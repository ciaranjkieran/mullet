from django.contrib import admin

from django.contrib import admin
from .models import Mode, Task

@admin.register(Mode)
class ModeAdmin(admin.ModelAdmin):
    list_display = ('id', 'title', 'color')  
    search_fields = ('title',)  

@admin.register(Task)
class TaskAdmin(admin.ModelAdmin):
    list_display = ('id', 'title', 'is_completed', 'mode')

