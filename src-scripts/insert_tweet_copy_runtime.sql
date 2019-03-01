CREATE OR REPLACE FUNCTION public.insert_today_tweet_runtime()
 RETURNS TRIGGER
 LANGUAGE plpgsql
AS $function$

    DECLARE
        today_day INTEGER;
        today_month INTEGER;
         
    BEGIN
        today_day = EXTRACT(DAY FROM now());
        IF today_day = EXTRACT(DAY FROM NEW.eventdate) THEN
            today_month = EXTRACT(MONTH FROM now());

            IF today_month = EXTRACT(MONTH FROM NEW.eventdate)  THEN --comparar con la fecha actual
                INSERT INTO public.today_tweets (tweet, eventdate, author, image1, image2, image3, image4, id, row_status)
                VALUES (NEW.tweet, NEW.eventdate, NEW.author, NEW.image1, NEW.image2, NEW.image3, NEW.image4, NEW.id, 0);
            END IF;
        END IF;

        RETURN NEW;

    END;
$function$
