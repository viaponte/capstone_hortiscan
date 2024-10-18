import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'io.ionic.starter',
  appName: 'PlataformaMovil',
  webDir: 'www',
  bundledWebRuntime: false,
  server: {
    cleartext: true,
    androidScheme: 'http'
  }
};

export default config;
