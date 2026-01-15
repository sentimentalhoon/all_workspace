// This file exists to satisfy browsers looking for the Service Worker from the previous PWA deployment.
// It explicitly unregisters itself to clear the old cache and ensure the new site loads correctly.

self.addEventListener("install", (e) => {
  self.skipWaiting();
});

self.addEventListener("activate", (e) => {
  e.waitUntil(
    self.registration
      .unregister()
      .then(() => {
        return self.clients.matchAll();
      })
      .then((clients) => {
        // Optional: Reload clients to ensure they are fresh
        // clients.forEach(client => client.navigate(client.url));
      })
  );
});
