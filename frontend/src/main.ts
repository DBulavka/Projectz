import { bootstrapApplication } from '@angular/platform-browser';
import { provideAnimations } from '@angular/platform-browser/animations';
import { definePreset } from '@primeuix/themes';
import Aura from '@primeuix/themes/aura';
import { providePrimeNG } from 'primeng/config';
import { AppComponent } from './app/app.component';

const AppPreset = definePreset(Aura, {
  semantic: {
    primary: {
      500: '{blue.600}'
    }
  }
});

bootstrapApplication(AppComponent, {
  providers: [
    provideAnimations(),
    providePrimeNG({
      theme: {
        preset: AppPreset
      }
    })
  ]
}).catch((err) => console.error(err));
