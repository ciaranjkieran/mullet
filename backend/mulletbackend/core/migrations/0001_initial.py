# Generated by Django 5.1.3 on 2025-02-23 01:38

import django.db.models.deletion
from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Mode',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('title', models.CharField(max_length=255)),
                ('color', models.CharField(default='#FFFFFF', max_length=7)),
                ('is_default', models.BooleanField(default=False)),
            ],
        ),
        migrations.CreateModel(
            name='Task',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('title', models.CharField(max_length=255)),
                ('description', models.TextField(blank=True, null=True)),
                ('time_logged', models.PositiveIntegerField(default=0)),
                ('is_completed', models.BooleanField(default=False)),
                ('mode', models.ForeignKey(default=1, on_delete=django.db.models.deletion.CASCADE, to='core.mode')),
            ],
        ),
    ]
