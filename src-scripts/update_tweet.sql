CREATE OR REPLACE FUNCTION public.update_tweet()
 RETURNS TRIGGER
 LANGUAGE plpgsql
AS $function$

    DECLARE
    
    BEGIN
        NEW.update_date = now(); --cambiar la fecha de actualización del registro
        RETURN NEW;

    END;
$function$

