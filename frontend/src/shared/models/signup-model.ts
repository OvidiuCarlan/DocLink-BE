export interface SignUpData {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    role: 'USER' | 'DOC';
  }