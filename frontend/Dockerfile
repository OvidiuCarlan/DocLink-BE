FROM node:18-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build --prod

FROM nginx:alpine
# Remove default nginx website
RUN rm -rf /usr/share/nginx/html/*

# Copy built app to nginx html directory
COPY --from=build /app/dist/doc-link/browser /usr/share/nginx/html/

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Make sure nginx can read the files
RUN chmod -R 755 /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]