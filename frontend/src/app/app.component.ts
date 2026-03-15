import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MenuItem } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { DrawerModule } from 'primeng/drawer';
import { InputTextModule } from 'primeng/inputtext';
import { MenuModule } from 'primeng/menu';
import { TableModule } from 'primeng/table';

interface ProcessItem {
  name: string;
  code: string;
  owner: string;
}

interface GameItem {
  title: string;
  genre: string;
  status: string;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, DrawerModule, ButtonModule, MenuModule, CardModule, InputTextModule, TableModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  sidebarVisible = true;
  activeSection: 'processes' | 'games' = 'processes';

  processForm: ProcessItem = { name: '', code: '', owner: '' };
  gameForm: GameItem = { title: '', genre: '', status: '' };

  processes: ProcessItem[] = [
    { name: 'Онбординг', code: 'PR-001', owner: 'HR Team' },
    { name: 'Согласование договора', code: 'PR-002', owner: 'Legal Team' }
  ];

  games: GameItem[] = [
    { title: 'Code Arena', genre: 'Puzzle', status: 'Active' },
    { title: 'QuestFlow', genre: 'Adventure', status: 'Draft' }
  ];

  navItems: MenuItem[] = [
    {
      label: 'Процессы',
      icon: 'pi pi-sitemap',
      command: () => this.activeSection = 'processes'
    },
    {
      label: 'Игры',
      icon: 'pi pi-play',
      command: () => this.activeSection = 'games'
    }
  ];

  addProcess(): void {
    if (!this.processForm.name || !this.processForm.code) {
      return;
    }

    this.processes = [...this.processes, { ...this.processForm }];
    this.processForm = { name: '', code: '', owner: '' };
  }

  addGame(): void {
    if (!this.gameForm.title || !this.gameForm.genre) {
      return;
    }

    this.games = [...this.games, { ...this.gameForm }];
    this.gameForm = { title: '', genre: '', status: '' };
  }
}
