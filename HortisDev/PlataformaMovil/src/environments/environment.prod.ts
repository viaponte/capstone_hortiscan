import { ipLocal } from "./ip-config";

export const environment = {
  production: true,
  apiUrl:  `http://${ ipLocal }:8080`
  // apiUrl:  `https://nest-scroll-beneficial-montreal.trycloudflare.com`
};