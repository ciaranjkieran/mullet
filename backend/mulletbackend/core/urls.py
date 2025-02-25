from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import ModeViewSet, TaskViewSet

router = DefaultRouter()

router.register('modes', ModeViewSet, basename='modes')
router.register('tasks', TaskViewSet, basename='tasks')

urlpatterns = [
    path('', include(router.urls)),
]
