#!/usr/bin/python

"""
How to terminate running Python threads using signals
https://www.g-loaded.eu/2016/11/24/how-to-terminate-running-python-threads-using-signals/
"""

import sys
import os
import time
import threading
import signal
import I2C_LCD_driver

# Constants
NUM_ARGUMENTS = 3

class LCDJob(threading.Thread):
    """
    Thread that loops showing the searched video on the LCD display
    """

    def __init__(self, video_title):
        threading.Thread.__init__(self)

        # The shutdown_flag is a threading.Event object that
        # indicates whether the thread should be terminated.
        self.shutdown_flag = threading.Event()

        # ... Other thread setup code here ...
        self.video_title = video_title

    def run(self):
        print 'Thread #%s started' % self.ident

        while not self.shutdown_flag.is_set():
            # ... Job code here ...
            time.sleep(0.5)

            mylcd = I2C_LCD_driver.lcd()

            str_pad = " " * 16
            my_long_string = str_pad + self.videoTitle

            while True and not self.shutdown_flag.is_set():
                mylcd.lcd_display_string("Playing now...", 2)

                for i in range(0, len(my_long_string)):
                    lcd_text = my_long_string[i:(i + 16)]
                    mylcd.lcd_display_string(lcd_text, 1)
                    time.sleep(0.3)
                    mylcd.lcd_display_string(str_pad, 1)

                    if self.shutdown_flag.is_set():
                        break

            # ... Clean shutdown code here ...
            print 'Thread #%s stopped' % self.ident

class YoutubeJob(threading.Thread):
    """
    Thread that plays the best result for the given title. It uses a shell
    script that requires omxplayer, youtube-dl, wget and curl
    """

    def __init__(self, video_title, lcd_thread):
        threading.Thread.__init__(self)

        # The shutdown_flag is a threading.Event object that
        # indicates whether the thread should be terminated.
        self.shutdown_flag = threading.Event()

        # ... Other thread setup code here ...
        self.video_title = video_title
        self.lcd_thread = lcd_thread

    def run(self):
        print 'Thread #%s started' % self.ident

        os.system("youtubeit " + "\"" + self.video_title + "\"")

        # ... Clean shutdown code here ...
        print 'Thread #%s stopped' % self.ident
        self.lcd_thread.shutdown_flag.set()


class ServiceExit(Exception):
    """
    Custom exception which is used to trigger the clean exit
    of all running threads and the main program.
    """
    pass


def service_shutdown(signum):
    """
    Throws a ServiceExit exception when a signal is registered
    """

    print 'Caught signal %d' % signum
    raise ServiceExit

def main():
    """
    Main
    """

    # Register the signal handlers
    signal.signal(signal.SIGTERM, service_shutdown)
    signal.signal(signal.SIGINT, service_shutdown)

    args_size = len(sys.argv)
    print "Number of arguments: " + str(args_size)

    def usage():
        """
        It prints the correct script arguments
        """
        print "Correct syntax to play a Youtube video:"
        print "\tpilcd -t \"title of the video\""


    if len(sys.argv) != NUM_ARGUMENTS:
        usage()
    else:
        title = sys.argv[2] # title to display on the LCD

        try:
            lcd_thread = LCDJob(title)
            lcd_thread.start()
            youtube_thread = YoutubeJob(title, lcd_thread)
            youtube_thread.start()

            # Keep the main thread running, otherwise signals are ignored.
            # while True:
            #    time.sleep(0.5)

        except ServiceExit:
            # Terminate the running threads.
            # Set the shutdown flag on each thread to trigger a clean shutdown of each thread.
            lcd_thread.shutdown_flag.set()
            youtube_thread.shutdown_flag.set()
            # Wait for the threads to close...
            lcd_thread.join()
            youtube_thread.join()

    print 'Exiting main program'

if __name__ == '__main__':
    main()
