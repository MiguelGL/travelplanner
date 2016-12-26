export interface User {
  id: number;
  updated: Date;

  email: string;

  role: string;

  firstName: string;
  lastName?: string;
}
